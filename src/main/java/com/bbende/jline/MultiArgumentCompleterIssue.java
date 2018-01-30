package com.bbende.jline;

import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MultiArgumentCompleterIssue {

    public static final String SHELL_NAME = "multi-arg-completer-issue";
    public static final String PROMPT = "#> ";

    public static void main(String[] args) throws IOException {
        try (Terminal terminal = TerminalBuilder.builder()
                .name(SHELL_NAME)
                .system(true)
                .nativeSignals(true)
                .signalHandler(Terminal.SignalHandler.SIG_IGN)
                .build();
             PrintStream output = new PrintStream(terminal.output(), true)) {

            // cmd1 -argA argAvalue -argB argBvalue -argC argCvalue
            StringsCompleter cmd1Completer = new StringsCompleter("cmd1");
            StringsCompleter cmd1ArgsCompleter = new StringsCompleter("-argA", "-argB", "-argC");
            ArgumentCompleter cmd1ArgumentCompleter = new ArgumentCompleter(cmd1Completer, cmd1ArgsCompleter);

            // cmd2 -argX argXvalue -argY argYvalue -argZ argZvalue
            StringsCompleter cmd2Completer = new StringsCompleter("cmd2");
            StringsCompleter cmd2ArgsCompleter = new StringsCompleter("-argX", "-argY", "-argZ");
            ArgumentCompleter cmd2ArgumentCompleter = new ArgumentCompleter(cmd2Completer, cmd2ArgsCompleter);

            // combine completers for cmd1 and cmd2
            List<Completer> completers = new ArrayList<>();
            completers.add(cmd1ArgumentCompleter);
            completers.add(cmd2ArgumentCompleter);

            AggregateCompleter aggregateCompleter = new AggregateCompleter(completers);

            LineReader reader = LineReaderBuilder.builder()
                    .appName(SHELL_NAME)
                    .terminal(terminal)
                    .completer(aggregateCompleter)
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
