package yajhfc.console;

import java.awt.Frame;
import java.util.logging.Logger;

import yajhfc.MainWin.SendReadyState;
import yajhfc.launch.MainApplicationFrame;
import yajhfc.ui.YajOptionPane;
import yajhfc.ui.console.ConsoleYajOptionPane;

public class ConsoleMainFrame implements MainApplicationFrame {
    private static final Logger log = Logger.getLogger(ConsoleMainFrame.class.getName());
    
    protected YajOptionPane dialogUI = new ConsoleYajOptionPane();
    
    @Override
    public Frame getFrame() {
        log.severe("getFrame() called for console application!");
        return null;
    }

    @Override
    public YajOptionPane getDialogUI() {
        return dialogUI;
    }

    @Override
    public void bringToFront() {
        log.warning("bringToFront() called for console application!");
    }

    @Override
    public void dispose() {
        log.warning("dispose() called for console application!");
    }

    @Override
    public SendReadyState getSendReadyState() {
        return SendReadyState.Ready;
    }

    @Override
    public void saveWindowSettings() {
        log.warning("saveWindowSettings() called for console application!");
    }

}
