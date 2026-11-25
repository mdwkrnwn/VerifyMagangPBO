package com.mycompany.verifymagang.view.common;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Halaman daftar berbasis tabel.
 *
 * <p>Menyatukan pola yang dipakai hampir semua halaman: toolbar di atas, tabel
 * yang bisa dicari, penanda jumlah data, dan tombol Ubah/Hapus yang otomatis
 * aktif hanya saat ada baris terpilih. Subclass cukup mengisi
 * {@link #muatData()} dan menjelaskan cara memetakan objek ke baris lewat
 * {@link #baris(Object)}.</p>
 *
 * <p>Objek model asli disimpan sejajar dengan baris tabel sehingga halaman tidak
 * perlu mengurai teks sel untuk mendapatkan id.</p>
 *
 * @param <T> tipe objek yang ditampilkan
 */
public abstract class TablePage<T> extends Page implements Refreshable {

    /** Tabel data halaman. */
    protected final JTable table;

    /** Model tabel halaman. */
    protected final DefaultTableModel model;

    /** Kolom pencarian. */
    protected final JTextField searchField;

    /** Label jumlah data. */
    protected final JLabel countLabel;

    private final JPanel toolbarLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final JPanel toolbarRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private final List<T> rows = new ArrayList<>();
    private final List<JComponent> selectionBound = new ArrayList<>();
    private final javax.swing.Timer debounce;
    private Runnable onSearch;
    private Runnable onSelectionChanged;

    /**
     * Membuat halaman tabel.
     *
     * @param judul    judul halaman
     * @param subjudul keterangan halaman
     * @param columns  nama-nama kolom
     */
    protected TablePage(String judul, String subjudul, String... columns) {
        super(judul, subjudul);

        model = Ui.tableModel(columns);
        table = new JTable(model);
        Ui.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        countLabel = Ui.caption("Memuat data...");
        searchField = Page.searchField(240);

        debounce = new javax.swing.Timer(260, e -> {
            if (onSearch != null) {
                onSearch.run();
            }
        });
        debounce.setRepeats(false);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                debounce.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                debounce.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                debounce.restart();
            }
        });

        toolbarLeft.setOpaque(false);
        toolbarRight.setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setBackground(Theme.Colors.CARD);
        toolbar.setBorder(new javax.swing.border.EmptyBorder(14, 16, 14, 16));
        toolbar.add(toolbarLeft, BorderLayout.WEST);
        toolbar.add(toolbarRight, BorderLayout.EAST);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setBackground(Theme.Colors.CARD);
        footer.setBorder(new javax.swing.border.EmptyBorder(10, 16, 10, 16));
        footer.add(countLabel);

        Ui.Card card = Ui.card();
        card.setLayout(new BorderLayout());
        card.add(toolbar, BorderLayout.NORTH);
        card.add(Ui.tableScroll(table), BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        setBody(card);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectionState();
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    onRowDoubleClick();
                }
            }
        });

        toolbarLeft.add(searchField);
    }

    // -------------------------------------------------------------- tata letak

    /**
     * Menambahkan komponen ke sisi kiri toolbar.
     *
     * @param components komponen
     */
    protected void addToolbarLeft(Component... components) {
        for (Component c : components) {
            toolbarLeft.add(c);
        }
    }

    /**
     * Menambahkan komponen ke sisi kanan toolbar.
     *
     * @param components komponen
     */
    protected void addToolbarRight(Component... components) {
        for (Component c : components) {
            toolbarRight.add(c);
        }
    }

    /**
     * Menyembunyikan kolom pencarian.
     */
    protected void hideSearch() {
        searchField.setVisible(false);
        toolbarLeft.remove(searchField);
    }

    /**
     * Mengatur lebar kolom.
     *
     * @param columnIndex indeks kolom
     * @param width       lebar piksel
     */
    protected void setColumnWidth(int columnIndex, int width) {
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        column.setPreferredWidth(width);
        column.setWidth(width);
    }

    /**
     * Memakai renderer status berwarna pada satu kolom.
     *
     * @param columnIndex indeks kolom status
     */
    protected void statusColumn(int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(Ui.statusRenderer());
    }

    /**
     * Memakai renderer tebal pada satu kolom.
     *
     * @param columnIndex indeks kolom
     */
    protected void boldColumn(int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(Ui.boldRenderer());
    }

    /**
     * Mendaftarkan komponen yang hanya aktif saat ada baris terpilih.
     *
     * @param components komponen
     */
    protected void bindToSelection(JComponent... components) {
        Collections.addAll(selectionBound, components);
        updateSelectionState();
    }

    /**
     * Menetapkan aksi tambahan saat pemilihan baris berubah.
     *
     * @param action aksi
     */
    protected void setOnSelectionChanged(Runnable action) {
        this.onSelectionChanged = action;
        updateSelectionState();
    }

    private void updateSelectionState() {
        boolean has = table.getSelectedRow() >= 0;
        for (JComponent c : selectionBound) {
            c.setEnabled(has);
        }
        if (onSelectionChanged != null) {
            onSelectionChanged.run();
        }
    }

    /** Dipanggil saat baris tabel diklik dua kali. */
    protected void onRowDoubleClick() {
        // bawaan: tidak melakukan apa pun
    }

    // ------------------------------------------------------------------ data

    /**
     * Mengisi tabel dengan data.
     *
     * @param items daftar objek
     */
    protected void isiTabel(List<T> items) {
        rows.clear();
        rows.addAll(items);
        model.setRowCount(0);
        for (T item : items) {
            model.addRow(baris(item));
        }
        countLabel.setText(items.size() + " data ditampilkan");
        updateSelectionState();
    }

    /**
     * Mengosongkan tabel dengan pesan tertentu.
     *
     * @param pesan pesan status
     */
    protected void kosongkan(String pesan) {
        rows.clear();
        model.setRowCount(0);
        countLabel.setText(pesan);
        updateSelectionState();
    }

    /**
     * Mengambil objek pada baris terpilih.
     *
     * @return objek terpilih, atau {@code null}
     */
    protected T terpilih() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= rows.size()) {
            return null;
        }
        return rows.get(modelRow);
    }

    /**
     * Mengambil seluruh objek yang sedang ditampilkan.
     *
     * @return daftar objek
     */
    protected List<T> semuaBaris() {
        return new ArrayList<>(rows);
    }

    /** @return teks kolom pencarian, sudah dirapikan */
    protected String kataKunci() {
        String text = searchField.getText();
        return text == null ? "" : text.trim();
    }

    /**
     * Menetapkan aksi yang dijalankan (dengan jeda) saat pencarian berubah.
     *
     * @param action aksi muat ulang
     */
    protected void setOnSearch(Runnable action) {
        this.onSearch = action;
    }

    /**
     * Memuat ulang data di latar belakang lalu mengisi tabel.
     */
    protected void muatUlang() {
        Async.run(this, this::muatData, this::isiTabel, this::tanganiGagalMuat);
    }

    private void tanganiGagalMuat(Throwable ex) {
        kosongkan("Gagal memuat data");
        Messages.error(this, Messages.pesanDari(ex));
    }

    @Override
    public void onShow() {
        muatUlang();
    }

    // ---------------------------------------------------------------- tombol

    /**
     * Membuat tombol "Tambah" standar.
     *
     * @param aksi aksi saat diklik
     * @return tombol
     */
    protected JButton tombolTambah(Runnable aksi) {
        Ui.FlatButton b = Ui.primaryButton("Tambah",
                Icons.get(Icons.I.PLUS, 16, java.awt.Color.WHITE));
        b.addActionListener(e -> aksi.run());
        return b;
    }

    /**
     * Membuat tombol "Ubah" standar (nonaktif sampai ada baris terpilih).
     *
     * @param aksi aksi saat diklik
     * @return tombol
     */
    protected JButton tombolUbah(Runnable aksi) {
        Ui.FlatButton b = Ui.secondaryButton("Ubah",
                Icons.get(Icons.I.EDIT, 15, Theme.Colors.TEXT));
        b.addActionListener(e -> aksi.run());
        b.setEnabled(false);
        bindToSelection(b);
        return b;
    }

    /**
     * Membuat tombol "Hapus" standar (nonaktif sampai ada baris terpilih).
     *
     * @param aksi aksi saat diklik
     * @return tombol
     */
    protected JButton tombolHapus(Runnable aksi) {
        Ui.FlatButton b = Ui.dangerButton("Hapus",
                Icons.get(Icons.I.DELETE, 15, Theme.Colors.DANGER));
        b.addActionListener(e -> aksi.run());
        b.setEnabled(false);
        bindToSelection(b);
        return b;
    }

    /**
     * Membuat tombol "Muat ulang" berikon.
     *
     * @return tombol
     */
    protected JButton tombolMuatUlang() {
        JButton b = Ui.iconButton(
                Icons.get(Icons.I.REFRESH, 17, Theme.Colors.TEXT_MUTED), "Muat ulang");
        b.addActionListener(e -> muatUlang());
        return b;
    }

    /**
     * Membuat tombol sekunder dengan ikon.
     *
     * @param teks teks tombol
     * @param ikon ikon
     * @param aksi aksi saat diklik
     * @return tombol
     */
    protected JButton tombolAksi(String teks, javax.swing.Icon ikon, Runnable aksi) {
        Ui.FlatButton b = Ui.secondaryButton(teks, ikon);
        b.addActionListener(e -> aksi.run());
        return b;
    }

    // -------------------------------------------------------------- abstrak

    /**
     * Mengambil data dari database. Dijalankan di luar thread UI.
     *
     * @return daftar data
     * @throws Exception bila query gagal
     */
    protected abstract List<T> muatData() throws Exception;

    /**
     * Mengubah satu objek menjadi sel-sel baris tabel.
     *
     * @param item objek
     * @return nilai tiap kolom
     */
    protected abstract Object[] baris(T item);
}
