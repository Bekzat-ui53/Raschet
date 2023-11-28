package it.talimbekov.raschet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResultEntry {
    private float height;
    private float width;
    private float count;
    private float countArea;
    private float itemPrice;
    private long timestamp;

    public ResultEntry(float height, float width, float count, float countArea, float itemPrice) {
        this.height = height;
        this.width = width;
        this.count = count;
        this.countArea = countArea;
        this.itemPrice = itemPrice;
        this.timestamp = System.currentTimeMillis();
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultdate = new Date(timestamp);
        return sdf.format(resultdate);
    }

    public String getResultEntryString() {
        return String.format("%.2f мм, %.2f мм, %.2f штук, %.2f м², %.2f тг", height, width, count, countArea, itemPrice);
    }
}
