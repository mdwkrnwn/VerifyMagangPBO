package com.mycompany.verifymagang.view.common;

import javax.swing.SwingWorker;
import java.awt.Component;
import java.util.function.Consumer;

/**
 * Pembantu menjalankan operasi database di luar Event Dispatch Thread.
 *
 * <p>Semua pemanggilan service dari halaman view dibungkus kelas ini sehingga
 * antarmuka tidak pernah membeku saat query berjalan.</p>
 */
public final class Async {

    private Async() {
    }

    /**
     * Pekerjaan yang menghasilkan nilai dan dapat melempar exception.
     *
     * @param <T> tipe hasil
     */
    @FunctionalInterface
    public interface Task<T> {

        /**
         * Menjalankan pekerjaan.
         *
         * @return hasil pekerjaan
         * @throws Exception bila gagal
         */
        T run() throws Exception;
    }

    /**
     * Menjalankan pekerjaan di latar belakang.
     *
     * @param parent  komponen induk untuk dialog error (boleh {@code null})
     * @param task    pekerjaan
     * @param onDone  aksi setelah berhasil (dipanggil di EDT)
     * @param <T>     tipe hasil
     */
    public static <T> void run(Component parent, Task<T> task, Consumer<T> onDone) {
        run(parent, task, onDone, null);
    }

    /**
     * Menjalankan pekerjaan di latar belakang dengan penanganan error khusus.
     *
     * @param parent  komponen induk untuk dialog error (boleh {@code null})
     * @param task    pekerjaan
     * @param onDone  aksi setelah berhasil (dipanggil di EDT)
     * @param onError aksi saat gagal; bila {@code null} dipakai dialog error standar
     * @param <T>     tipe hasil
     */
    public static <T> void run(Component parent, Task<T> task,
            Consumer<T> onDone, Consumer<Throwable> onError) {

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.run();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    if (onDone != null) {
                        onDone.accept(result);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    handle(ex, parent, onError);
                } catch (java.util.concurrent.ExecutionException ex) {
                    handle(ex.getCause() != null ? ex.getCause() : ex, parent, onError);
                } catch (java.util.concurrent.CancellationException ex) {
                    // dibatalkan, tidak perlu ditampilkan
                }
            }
        }.execute();
    }

    private static void handle(Throwable ex, Component parent, Consumer<Throwable> onError) {
        if (onError != null) {
            onError.accept(ex);
            return;
        }
        // Pelanggaran aturan bisnis bukan kegagalan sistem, jadi ditampilkan
        // sebagai peringatan agar pengguna tahu masalahnya ada pada input.
        if (ex instanceof IllegalStateException || ex instanceof IllegalArgumentException) {
            Messages.peringatan(parent, Messages.pesanDari(ex));
        } else {
            Messages.error(parent, Messages.pesanDari(ex));
        }
    }
}
