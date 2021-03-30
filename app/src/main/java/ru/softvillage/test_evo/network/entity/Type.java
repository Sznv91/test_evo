
package ru.softvillage.test_evo.network.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Type {

    @SerializedName("number")
    @Expose
    public Integer number;
    @SerializedName("mark_info")
    @Expose
    public Object markInfo;

}
