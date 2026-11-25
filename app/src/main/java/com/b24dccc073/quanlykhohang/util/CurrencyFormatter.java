package com.b24dccc073.quanlykhohang.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class CurrencyFormatter {
    private static final DecimalFormat VND_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        VND_FORMAT = new DecimalFormat("#,###", symbols);
    }

    private CurrencyFormatter() {
    }

    public static String formatVnd(long amount) {
        return VND_FORMAT.format(amount) + " đ";
    }
}
