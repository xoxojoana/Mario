package ch.idsia.scenarios;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch
 * Date: Mar 17, 2010 Time: 8:28:00 AM Package: ch.idsia.scenarios
 */
public final class Main {
    public static void main(String[] args) {
//        final String argsString = "-vis on";
//    String test ="-ag dk.itu.mcts.agent.MCTSAgent -vlx 330 -vly 290 -ld 1 -ls 42 -ll 256 -lt 1";
        String test = "-ag dk.itu.mcts.agent.MCTSAgent -vis on";
        final MarioAIOptions marioAIOptions = new MarioAIOptions(test);
//        final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
//        final Environment environment = new MarioEnvironment();
//        final Agent agent = new ForwardAgent();
//        final Agent agent = marioAIOptions.getAgent();
//        final Agent a = AgentsPool.loadAgent("ch.idsia.controllers.agents.controllers.ForwardJumpingAgent");
        final BasicTask basicTask = new BasicTask(marioAIOptions);
//        for (int i = 0; i < 10; ++i)
//        {
//            int seed = 0;
//            do
//            {
//                marioAIOptions.setLevelDifficulty(i);
//                marioAIOptions.setLevelRandSeed(seed++);
        basicTask.setOptionsAndReset(marioAIOptions);
//    basicTask.runSingleEpisode(1);
        basicTask.doEpisodes(1, true, 1);
//    System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
//            } while (basicTask.getEnvironment().getEvaluationInfo().marioStatus != Environment.MARIO_STATUS_WIN);
//        }
//
        System.exit(0);
    }

}
