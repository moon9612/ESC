package com.esc.wmg.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TblNewsClusterId implements Serializable {
    private String date; // String으로 변경
    private Integer mdlIndex;
    private Integer seq;
}
