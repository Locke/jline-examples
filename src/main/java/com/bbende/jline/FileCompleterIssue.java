package com.bbende.jline;

import org.jline.builtins.Completers;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileCompleterIssue {

    public static final String SHELL_NAME = "file-completer-issue";
    public static final String PROMPT = "#> ";

    public static void main(String[] args) throws IOException {
        try (Terminal terminal = TerminalBuilder.builder()
                .name(SHELL_NAME)
                .system(true)
                .nativeSignals(true)
                .signalHandler(Terminal.SignalHandler.SIG_IGN)
                .build();
            PrintStream output = new PrintStream(terminal.output(), true)) {

            Completer fileNameCompleter = new Completers.FileNameCompleter();

            LineReader reader = LineReaderBuilder.builder()
                    .appName(SHELL_NAME)
                    .terminal(terminal)
                    .completer(fileNameCompleter)
                    .build();

            reader.setOpt(LineReader.Option.AUTO_FRESH_LINE);
            reader.unsetOpt(LineReader.Option.INSERT_TAB);

            while (true) {
                try {
                    final String line = reader.readLine(PROMPT);
                    if (line == null || line.trim().isEmpty()) {
                        continue;
                    }

                    if ("exit".equalsIgnoreCase(line.trim())) {
                        System.exit(0);
                    }

                    ParsedLine parsedLine = reader.getParsedLine();
                    String[] parsedArgs = parsedLine.words().toArray(new String[parsedLine.words().size()]);

                    for (String arg : parsedArgs) {
                        output.println("Parsed Arg: " + arg);
                    }

                } catch (UserInterruptException e) {
                    // Ignore
                } catch (EndOfFileException e) {
                    return;
                }
            }
        }
    }

}
