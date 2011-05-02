/**
 *  Copyright (c) 1999-2011, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package parser.flatzinc;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import parser.flatzinc.parser.FZNParser;
import solver.Solver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 27/01/11
 */
public class ParseAndSolve {

    @Option(name = "-f", usage = "Flatzinc file.", required = true)
    private static String instance;

    @Option(name = "-tl", usage = "Time limit.", required = false)
    private static long tl = -1;


    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        new ParseAndSolve().doMain(args);
    }

    public void doMain(String[] args) throws IOException {
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(160);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java SampleMain [options...]");
            parser.printUsage(System.err);
            System.err.println("\nCheck MiniZinc is correctly installed.");
            System.err.println();
            return;
        }
        parseandsolve();
    }

    private void parseandsolve() {
        FZNParser parser = new FZNParser();
        parser.loadInstance(new File(instance));
        parser.parse();
        Solver solver = parser.solver;
        parser = null;
        if(tl > -1){
            solver.getSearchLoop().getLimitsFactory().setTimeLimit(tl);
        }
        solver.solve();
    }

}