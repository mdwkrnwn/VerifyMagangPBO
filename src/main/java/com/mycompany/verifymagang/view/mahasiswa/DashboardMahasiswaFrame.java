package com.mycompany.verifymagang.view.mahasiswa;

/**
 * Kerangka form dashboard mahasiswa.
 *
 * <p>Kelas ini sengaja belum memiliki komponen apa pun. Buka file ini di
 * NetBeans, klik tab <em>Design</em>, lalu rancang tampilannya. NetBeans akan
 * membuat file {@code .form} secara otomatis.</p>
 *
 * <p>Hubungkan komponen ke lapisan bisnis melalui service, misalnya:</p>
 * <pre>
 * LowonganService lowonganService = new LowonganService();
 * int mahasiswaId = SessionManager.getMahasiswaId();
 * </pre>
 */
public class DashboardMahasiswaFrame extends javax.swing.JFrame {

    /** Membuat form dashboard mahasiswa. */
    public DashboardMahasiswaFrame() {
        initComponents();
    }

    /**
     * Metode inisialisasi komponen. Jangan diubah manual; NetBeans GUI Builder
     * akan menuliskannya ulang saat Anda memakai tab Design.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
