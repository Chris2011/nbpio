package nbpio.serialmonitor.ui;
/** Localizable strings for {@link nbpio.serialmonitor.ui}. */
class Bundle {
    /**
     * @return <i>SerialMonitorDisplayPane</i>
     * @see SerialMonitorDisplayPaneTopComponent
     */
    static String CTL_SerialMonitorDisplayPaneAction() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "CTL_SerialMonitorDisplayPaneAction");
    }
    /**
     * @return <i>SerialMonitorDisplayPane Window</i>
     * @see SerialMonitorDisplayPaneTopComponent
     */
    static String CTL_SerialMonitorDisplayPaneTopComponent() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "CTL_SerialMonitorDisplayPaneTopComponent");
    }
    /**
     * @return <i>This is a SerialMonitorDisplayPane window</i>
     * @see SerialMonitorDisplayPaneTopComponent
     */
    static String HINT_SerialMonitorDisplayPaneTopComponent() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "HINT_SerialMonitorDisplayPaneTopComponent");
    }
    private Bundle() {}
}
