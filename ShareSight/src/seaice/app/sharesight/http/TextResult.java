package seaice.app.sharesight.http;

import android.os.Bundle;

public class TextResult {

    private String text;

    private Bundle data;

    public TextResult(String text, Bundle data) {
        this.text = text;
        this.data = data;
    }

    public String getText() {
        return text;
    }

    public Bundle getData() {
        return data;
    }
}
