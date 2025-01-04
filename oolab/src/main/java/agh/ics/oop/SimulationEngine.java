package agh.ics.oop;

import agh.ics.oop.model.EquatorialForest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {
    private List<Simulation> simulations;
    private List<Thread> threads = new ArrayList<>();
    private ExecutorService pool = Executors.newFixedThreadPool(4);

    public SimulationEngine(List<Simulation> simulations) {
        this.simulations = simulations;
    }

    public void runSync() {
        for (Simulation sim : simulations) {
            sim.run();

            // Losowy wzrost roślin po każdym ruchu
            if (sim.getMap() instanceof EquatorialForest) {
                ((EquatorialForest) sim.getMap()).growPlants(); // Domyślne wywołanie
            }
        }
    }


    public void runAsync() {
        for (Simulation sim : simulations) {
            Thread engineThread = new Thread(sim);
            threads.add(engineThread);
            engineThread.start();
        }
        awaitSimulationEnd();
    }

    public void runAsyncInThreadPool() {
        for (Simulation sim : simulations) {
            pool.submit(sim);
        }
        awaitSimulationEnd();
    }

    public void awaitSimulationEnd() {
        try {
            for (Thread thread : threads) {
                thread.join();
            }
            pool.shutdown();
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
