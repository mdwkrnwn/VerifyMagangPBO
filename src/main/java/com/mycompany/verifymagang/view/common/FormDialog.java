package com.mycompany.verifymagang.view.common;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Dialog modal untuk form tambah/ubah data.
 *
 * <p>Hanya menyediakan kerangka: judul, daftar field, dan tombol Simpan/Batal.
 * Pemanggil menambahkan field lalu membaca nilainya setelah dialog ditutup.</p>
 *
 * <pre>{@code
 * FormDialog dlg = new FormDialog(owner, "Tambah Mahasiswa", "Lengkapi data berikut");
 * JTextField nama = Ui.textField(20);
 * dlg.addField("Nama", nama);
 * if (dlg.showDialog()) {
 *     // baca nama.getText()
 * }
 * }</pre>
 */
public class FormDialog extends JDialog {

    private final JPanel fieldsPanel;
    private final JScrollPane fieldsScroll;
    private boolean saved;

    /**
     * Membuat dialog form.
     *
     * @param owner   jendela induk
     * @param judul   judul dialog
     * @param subjudul keterangan di bawah judul (boleh {@code null})
     */
    public FormDialog(Window owner, String judul, String subjudul) {
        super(owner, judul, ModalityType.APPLICATION_MODAL);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.Colors.CARD);
        root.setBorder(new EmptyBorder(24, 26, 20, 26));

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(judul);
        titleLabel.setFont(Theme.FONT_SECTION);
        titleLabel.setForeground(Theme.Colors.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(titleLabel);

        if (subjudul != null && !subjudul.isBlank()) {
            JLabel subLabel = new JLabel(subjudul);
            subLabel.setFont(Theme.FONT_SMALL);
            subLabel.setForeground(Theme.Colors.TEXT_MUTED);
            subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            header.add(Box.createVerticalStrut(4));
            header.add(subLabel);
        }
        header.add(Box.createVerticalStrut(18));
        root.add(header, BorderLayout.NORTH);

        // Fields
        fieldsPanel = new JPanel();
        fieldsPanel.setOpaque(false);
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(fieldsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setPreferredSize(new Dimension(460, 360));
        fieldsScroll = scroll;
        root.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));

        Ui.FlatButton cancel = Ui.secondaryButton("Batal", null);
        cancel.addActionListener(e -> dispose());

        Ui.FlatButton save = Ui.primaryButton("Simpan", Icons.get(Icons.I.CHECK, 16, java.awt.Color.WHITE));
        save.addActionListener(e -> {
            saved = true;
            dispose();
        });

        footer.add(cancel);
        footer.add(save);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);

        // Enter = simpan, Escape = batal
        getRootPane().setDefaultButton(save);
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    /**
     * Menambahkan satu field berlabel.
     *
     * @param label     teks label
     * @param component komponen input
     * @return komponen input yang sama (memudahkan chaining)
     * @param <T> tipe komponen
     */
    public <T extends JComponent> T addField(String label, T component) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.Colors.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension pref = component.getPreferredSize();
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));

        wrapper.add(l);
        wrapper.add(Box.createVerticalStrut(6));
        wrapper.add(component);
        wrapper.add(Box.createVerticalStrut(14));

        fieldsPanel.add(wrapper);
        return component;
    }

    /**
     * Menambahkan dua field berdampingan dalam satu baris.
     *
     * @param labelKiri   label kiri
     * @param kiri        komponen kiri
     * @param labelKanan  label kanan
     * @param kanan       komponen kanan
     */
    public void addFieldPair(String labelKiri, JComponent kiri, String labelKanan, JComponent kanan) {
        JPanel row = new JPanel(new java.awt.GridLayout(1, 2, 12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel l1 = new JLabel(labelKiri);
        l1.setFont(Theme.FONT_LABEL);
        l1.setForeground(Theme.Colors.TEXT_MUTED);
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);
        kiri.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(l1);
        left.add(Box.createVerticalStrut(6));
        left.add(kiri);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel l2 = new JLabel(labelKanan);
        l2.setFont(Theme.FONT_LABEL);
        l2.setForeground(Theme.Colors.TEXT_MUTED);
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);
        kanan.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(l2);
        right.add(Box.createVerticalStrut(6));
        right.add(kanan);

        row.add(left);
        row.add(right);

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        wrapper.add(row);
        wrapper.add(Box.createVerticalStrut(14));
        fieldsPanel.add(wrapper);
    }

    /**
     * Menampilkan dialog secara modal.
     *
     * <p>Tinggi area field disesuaikan dengan jumlah isian agar form pendek tidak
     * menyisakan ruang kosong dan form panjang tetap bisa digulir.</p>
     *
     * @return {@code true} bila pengguna menekan Simpan
     */
    public boolean showDialog() {
        saved = false;
        sesuaikanTinggi();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return saved;
    }

    /** Menghitung ulang tinggi area field berdasarkan isinya. */
    private void sesuaikanTinggi() {
        fieldsPanel.doLayout();
        int tinggiIsi = 0;
        for (Component c : fieldsPanel.getComponents()) {
            tinggiIsi += c.getPreferredSize().height;
        }
        int tinggi = Math.max(120, Math.min(tinggiIsi + 4, 430));
        fieldsScroll.setPreferredSize(new Dimension(460, tinggi));
        pack();
    }
}
