/*  Copyright (C) 2003-2011 JabRef contributors.
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package net.sf.jabref.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.sf.jabref.Globals;

/**
 * This class redirects the System.err stream so it goes both the way it normally
 * goes, and into a ByteArrayOutputStream. We can use this stream to display any
 * error messages and stack traces to the user. Such an error console can be
 * useful in getting complete bug reports, especially from Windows users,
 * without asking users to run JabRef in a command window to catch the error info.
 *
 * User: alver
 * Date: Mar 1, 2006
 * Time: 11:13:03 PM
 */
public class ErrorConsole {

    ByteArrayOutputStream errByteStream = new ByteArrayOutputStream();
    ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
    private static ErrorConsole instance = null;


    public static ErrorConsole getInstance() {
        if (instance == null)
            instance = new ErrorConsole();

        return instance;
    }

    private ErrorConsole() {
        PrintStream myErr = new PrintStream(errByteStream);
        PrintStream tee = new TeeStream(System.err, myErr);
        System.setErr(tee);
        myErr = new PrintStream(outByteStream);
        tee = new TeeStream(System.out, myErr);
        System.setOut(tee);
    }

    public String getErrorMessages() {
        return errByteStream.toString();
    }

    public String getOutput() {
        return outByteStream.toString();
    }

    // All writes to this print stream are copied to two print streams
    public class TeeStream extends PrintStream {
        PrintStream out;
        public TeeStream(PrintStream out1, PrintStream out2) {
            super(out1);
            this.out = out2;
        }
        public void write(byte buf[], int off, int len) {
            try {
                super.write(buf, off, len);
                out.write(buf, off, len);
            } catch (Exception e) {
            }
        }
        public void flush() {
            super.flush();
            out.flush();
        }
    }
}
