package com.mutunus.tutunus.structures;

public class FlowMetaInfo {

    private final MTDate from;
    private final MTDate to;
    private final String creatorInfo;

    public static final FlowMetaInfo NO_INFO = new FlowMetaInfo(MTDate.DAY_ONE, MTDate.DAY_ONE, "");

    public FlowMetaInfo(final MTDate fromData, final MTDate toDate, final String creatorInfo) {
        from = fromData;
        to = toDate;
        this.creatorInfo = creatorInfo;
    }

    public MTDate getFromDate() {
        return from;
    }

    public MTDate getToDate() {
        return to;
    }

    public String getCreatorInfo() {
        return creatorInfo;
    }

}
