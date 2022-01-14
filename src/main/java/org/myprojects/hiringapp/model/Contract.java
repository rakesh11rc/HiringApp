package org.myprojects.hiringapp.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Data
@Builder
@Jacksonized
public class Contract {

    private String id;

    @NonNull
    private Date beginDate;

    private Date endDate;

    private double compensation;

    private String info;

}
