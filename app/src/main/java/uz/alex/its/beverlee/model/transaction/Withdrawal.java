package uz.alex.its.beverlee.model.transaction;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Withdrawal {
    @Expose
    @SerializedName("request")
    private final Request request;

    public Withdrawal(final Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    @NonNull
    @Override
    public String toString() {
        return "Withdrawal{" +
                "request=" + request +
                '}';
    }

    public static class Request {
        @Expose
        @SerializedName("method_label")
        private final String methodLabel;

        public Request(final String methodLabel) {
            this.methodLabel = methodLabel;
        }

        public String getMethodLabel() {
            return methodLabel;
        }

        @NonNull
        @Override
        public String toString() {
            return "Request{" +
                    "methodLabel='" + methodLabel + '\'' +
                    '}';
        }
    }
}
