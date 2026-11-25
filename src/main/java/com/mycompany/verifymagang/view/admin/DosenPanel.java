package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.model.Dosen;
import com.mycompany.verifymagang.service.DosenService;
import com.mycompany.verifymagang.view.common.Async;
import com.mycompany.verifymagang.view.common.FormDialog;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.TablePage;
import com.mycompany.verifymagang.view.common.Ui;

import javax.swing.JButton;
import javax.swing.JTextField;
import java.util.List;

/**
 * Halaman pengelolaan data dosen pembimbing.
 */
public class DosenPanel extends TablePage<Dosen> {

    private final DosenService service = new DosenService();

    /** Membuat halaman data dosen. */
    public DosenPanel() {
        super("Data Dosen", "Kelola data dosen pembimbing magang",
                "NIP", "Nama", "Bidang Keahlian", "No. HP", "Email");

        setColumnWidth(0, 130);
        setColumnWidth(1, 210);
        setColumnWidth(2, 200);
        setColumnWidth(3, 140);
        boldColumn(1);

        JButton tambah = tombolTambah(this::dialogTambah);
        JButton ubah = tombolUbah(this::dialogUbah);
        JButton hapus = tombolHapus(this::hapusData);

        addToolbarRight(tambah, ubah, hapus, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<Dosen> muatData() throws Exception {
        return service.cari(kataKunci());
    }

    @Override
    protected Object[] baris(Dosen d) {
        return new Object[]{d.getNip(), d.getNama(), d.getBidangKeahlian(),
            d.getNoHp(), d.getEmail()};
    }

    @Override
    protected void onRowDoubleClick() {
        dialogUbah();
    }

    private void dialogTambah() {
        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Tambah Dosen", "Lengkapi identitas dosen pembimbing.");

        JTextField nip = dialog.addField("NIP", Ui.textField(20));
        JTextField nama = dialog.addField("Nama Lengkap", Ui.textField(20));
        JTextField bidang = dialog.addField("Bidang Keahlian", Ui.textField(20));
        JTextField noHp = dialog.addField("No. HP", Ui.textField(20));
        JTextField email = dialog.addField("Email", Ui.textField(20));

        if (!dialog.showDialog()) {
            return;
        }
        if (kosong(nip.getText()) || kosong(nama.getText())) {
            Messages.peringatan(this, "NIP dan Nama wajib diisi.");
            return;
        }

        Dosen d = new Dosen();
        d.setNip(nip.getText().trim());
        d.setNama(nama.getText().trim());
        d.setBidangKeahlian(bidang.getText().trim());
        d.setNoHp(noHp.getText().trim());
        d.setEmail(email.getText().trim());

        Async.run(this,
                () -> service.tambahDosen(d),
                id -> {
                    Messages.sukses(this, "Dosen berhasil ditambahkan.");
                    muatUlang();
                });
    }

    private void dialogUbah() {
        Dosen selected = terpilih();
        if (selected == null) {
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Ubah Data Dosen", "Perbarui identitas dosen.");

        JTextField nip = dialog.addField("NIP", Ui.textField(20));
        nip.setText(selected.getNip());
        JTextField nama = dialog.addField("Nama Lengkap", Ui.textField(20));
        nama.setText(selected.getNama());
        JTextField bidang = dialog.addField("Bidang Keahlian", Ui.textField(20));
        bidang.setText(selected.getBidangKeahlian());
        JTextField noHp = dialog.addField("No. HP", Ui.textField(20));
        noHp.setText(selected.getNoHp());
        JTextField email = dialog.addField("Email", Ui.textField(20));
        email.setText(selected.getEmail());

        if (!dialog.showDialog()) {
            return;
        }
        if (kosong(nip.getText()) || kosong(nama.getText())) {
            Messages.peringatan(this, "NIP dan Nama wajib diisi.");
            return;
        }

        selected.setNip(nip.getText().trim());
        selected.setNama(nama.getText().trim());
        selected.setBidangKeahlian(bidang.getText().trim());
        selected.setNoHp(noHp.getText().trim());
        selected.setEmail(email.getText().trim());

        Async.run(this,
                () -> service.ubahDosen(selected),
                ok -> {
                    Messages.sukses(this, "Data dosen diperbarui.");
                    muatUlang();
                });
    }

    private void hapusData() {
        Dosen selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!Messages.konfirmasiHapus(this, "dosen " + selected.getNama())) {
            return;
        }
        Async.run(this,
                () -> service.hapusDosen(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Dosen dihapus.");
                    muatUlang();
                });
    }

    private static boolean kosong(String teks) {
        return teks == null || teks.isBlank();
    }
}
