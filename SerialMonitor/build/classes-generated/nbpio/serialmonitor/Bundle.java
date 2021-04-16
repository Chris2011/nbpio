package nbpio.serialmonitor;
/** Localizable strings for {@link nbpio.serialmonitor}. */
class Bundle {
    /**
     * @return <i>Serial Monitor</i>
     * @see SerialMonitorTopComponent
     */
    static String CTL_SerialMonitorAction() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "CTL_SerialMonitorAction");
    }
    /**
     * @return <i>Serial Monitor</i>
     * @see SerialMonitorTopComponent
     */
    static String CTL_SerialMonitorTopComponent() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "CTL_SerialMonitorTopComponent");
    }
    /**
     * @return <i>This is a SerialMonitor window</i>
     * @see SerialMonitorTopComponent
     */
    static String HINT_SerialMonitorTopComponent() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "HINT_SerialMonitorTopComponent");
    }
    private Bundle() {}
}
