package fr.xebia.futurecity.model;

public enum Position {

    P1("P1", null, BeaconMajor.P2, null, 90),
    P2("P2", BeaconMajor.P1, BeaconMajor.P3, -90, 0),
    P3("P3", BeaconMajor.P2, BeaconMajor.P4, 180, 45),
    P4("P4", BeaconMajor.P3, null, -135, null),
    UNKNOWN("UNKNOWN", null, null, null, null);

    private String positionName;
    private Integer previousPosition;
    private Integer nextPosition;
    private Integer angleWithPrevious;
    private Integer angleWithNext;

    Position(String positionName, Integer previousPosition, Integer nextPosition, Integer angleWithPrevious, Integer angleWithNext) {
        this.positionName = positionName;
        this.previousPosition = previousPosition;
        this.nextPosition = nextPosition;
        this.angleWithPrevious = angleWithPrevious;
        this.angleWithNext = angleWithNext;
    }

    public String getPositionName() {
        return positionName;
    }

    public Integer getPreviousPosition() {
        return previousPosition;
    }

    public Integer getNextPosition() {
        return nextPosition;
    }

    public Integer getAngleWithPrevious() {
        return angleWithPrevious;
    }

    public Integer getAngleWithNext() {
        return angleWithNext;
    }

    public static Position getPositionByMajor(int major) {
        if (major == BeaconMajor.P1) {
            return P1;
        }
        if (major == BeaconMajor.P2) {
            return P2;
        }
        if (major == BeaconMajor.P3) {
            return P3;
        }
        if (major == BeaconMajor.P4) {
            return P4;
        }
        return UNKNOWN;
    }
}
