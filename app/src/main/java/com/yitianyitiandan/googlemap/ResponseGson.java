package com.yitianyitiandan.googlemap;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseGson {
    @SerializedName("success")
    public boolean isSuccess;
    public Result result;

    public class Result {
        @SerializedName("resource_id")
        public String id;
        public List<Records> records;
        public int offset;
        public int total;
        public int limit;
    }

    public class Records {
        @SerializedName("地區")
        public String zone;
        @SerializedName("電話(公)")
        public String tel;
        @SerializedName("地址")
        public String address;
        @SerializedName("名稱")
        public String name;
    }
}
