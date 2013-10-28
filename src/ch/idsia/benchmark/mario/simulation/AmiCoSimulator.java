package ch.idsia.benchmark.mario.simulation;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import ch.idsia.tools.MarioAIOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey at idsia dot ch
 * Date: Mar 1, 2010 Time: 1:50:37 PM
 * Package: ch.idsia.scenarios
 */

public class AmiCoSimulator
{
public static void main(String[] args)
{
    MarioAIOptions marioAIOptions = new MarioAIOptions("");
    marioAIOptions.setMarioInvulnerable(true);
    String options = "-lf on -zs 1 -ls 16 -vis 1";
    System.out.print(options);
    Environment environment = MarioEnvironment.getInstance();
    Agent agent = new ForwardAgent();
    environment.reset(options);
    while (!environment.isLevelFinished())
    {
        environment.tick();
//                agent.integrateObservation(environment.getSerializedLevelSceneObservationZ(options[17]),
//                                           environment.getSerializedEnemiesObservationZ(options[18]),
//                                           environment.getMarioFloatPos(),
//                                           environment.getEnemiesFloatPos(),
//                                           environment.getMarioState());
        agent.integrateObservation(environment);
        environment.performAction(agent.getAction());
    }
    System.out.println("Evaluation Info:");
    int[] ev = environment.getEvaluationInfoAsInts();
    for (int anEv : ev)
    {
        System.out.print(anEv + ", ");
    }
//        }
    System.exit(0);
}

}