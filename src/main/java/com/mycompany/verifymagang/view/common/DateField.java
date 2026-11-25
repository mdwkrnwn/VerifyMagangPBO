package com.mycompany.verifymagang.view.common;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.JFormattedTextField;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Kolom tanggal berbasis spinner dengan format {@code dd-MM-yyyy}.
 *
 * <p>Nilai selalu dibaca sebagai {@link LocalDate} sehingga form tidak perlu
 * mem-parse teks tanggal sendiri.</p>
 */
public class DateField extends JSpinner {

    private static final String PATTERN = "dd-MM-yyyy";

    /** Membuat kolom tanggal dengan nilai awal hari ini. */
    public DateField() {
        this(LocalDate.now());
    }

    /**
     * Membuat kolom tanggal dengan nilai awal tertentu.
     *
     * @param awal tanggal awal (boleh {@code null} untuk hari ini)
     */
    public DateField(LocalDate awal) {
        super(new SpinnerDateModel());
        LocalDate nilai = awal == null ? LocalDate.now() : awal;
        setValue(toDate(nilai));
        setEditor(new JSpinner.DateEditor(this, PATTERN));
        setFont(Theme.FONT_BODY);
        setPreferredSize(new java.awt.Dimension(160, 38));
        JComponent editor = getEditor();
        if (editor instanceof JSpinner.DateEditor de) {
            JFormattedTextField tf = de.getTextField();
            tf.setFont(Theme.FONT_BODY);
            tf.setForeground(Theme.Colors.TEXT);
            tf.setBorder(new javax.swing.border.EmptyBorder(6, 8, 6, 8));
        }
    }

    /**
     * Mengambil nilai tanggal.
     *
     * @return tanggal terpilih
     */
    public LocalDate getLocalDate() {
        Object value = getValue();
        if (value instanceof Date date) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    /**
     * Menetapkan nilai tanggal.
     *
     * @param tanggal tanggal baru
     */
    public void setLocalDate(LocalDate tanggal) {
        if (tanggal != null) {
            setValue(toDate(tanggal));
        }
    }

    private static Date toDate(LocalDate tanggal) {
        Calendar cal = Calendar.getInstance();
        cal.set(tanggal.getYear(), tanggal.getMonthValue() - 1, tanggal.getDayOfMonth(), 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Mengubah {@link LocalDate} menjadi teks berformat.
     *
     * @param tanggal tanggal
     * @return teks tanggal
     */
    public static String format(LocalDate tanggal) {
        return tanggal == null ? "-" : new SimpleDateFormat(PATTERN).format(toDate(tanggal));
    }
}
