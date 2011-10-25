package yajhfc.console;
/*
 * YAJHFC - Yet another Java Hylafax client
 * Copyright (C) 2011 Jonas Wolz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
