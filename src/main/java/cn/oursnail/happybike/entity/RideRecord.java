package cn.oursnail.happybike.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RideRecord {
    private Long id;

    private Long userid;

    private String recordNo;

    private Long bikeNo;

    private Date startTime;

    private Date endTime;

    private Integer rideTime;

    private BigDecimal rideCost;

    private Byte status;
}