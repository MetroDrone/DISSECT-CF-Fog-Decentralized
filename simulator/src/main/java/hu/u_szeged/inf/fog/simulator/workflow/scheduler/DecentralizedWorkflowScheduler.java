package hu.u_szeged.inf.fog.simulator.workflow.scheduler;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.u_szeged.inf.fog.simulator.iot.Actuator;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;
import hu.u_szeged.inf.fog.simulator.provider.Instance;
import hu.u_szeged.inf.fog.simulator.workflow.DecentralizedWorkflowExecutor;
import hu.u_szeged.inf.fog.simulator.workflow.WorkflowJob;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;



public class DecentralizedWorkflowScheduler extends WorkflowScheduler {

    public int defaultLatency;
    public HashMap<ComputingAppliance, Instance> workflowArchitecture;

    public ArrayList<Actuator> actuatorArchitecture;
    public ArrayList<WorkflowJob> workflowJobs = new ArrayList<>();

    public DecentralizedWorkflowScheduler(HashMap<ComputingAppliance, Instance> workflowArchitecture,
                                ArrayList<Actuator> actuatorArchitecutre, int defaultLatency) {
        this.workflowArchitecture = workflowArchitecture;
        this.actuatorArchitecture = actuatorArchitecutre;
        this.defaultLatency = defaultLatency;
    }

    @Override
    public void schedule(WorkflowJob workflowJob) {
        if (workflowJob.id.contains("service")) {
            if (workflowJob.ca == null) {
                Random r = new Random();
                workflowJob.ca = ComputingAppliance.allComputingAppliances
                        .get(r.nextInt(WorkflowScheduler.workflowArchitecture.keySet().size()));
            }
            if (workflowJob.inputs.get(0).amount == 0) {
                workflowJob.ca.workflowQueue.add(workflowJob);
            }
        }
        if (workflowJob.id.contains("actuator")) {
            if (workflowJob.actuator == null) {
                Random r = new Random();
                workflowJob.actuator = Actuator.allActuators.get(r.nextInt(Actuator.allActuators.size()));
            }
            if (workflowJob.inputs.get(0).amount == 0) {
                workflowJob.actuator.actuatorWorkflowQueue.add(workflowJob);
            }

            if (Timed.getFireCount() > 0) {
                // this.actuatorReAssign(workflowJob, Actuator.allActuator.get(0));
            }
        }

    }
    public void schedule() {
        for(WorkflowJob workflowJob : workflowJobs){
            if (workflowJob.id.contains("service")) {
                if (workflowJob.ca == null) {
                    Random r = new Random();
                    workflowJob.ca = ComputingAppliance.allComputingAppliances
                            .get(r.nextInt(this.workflowArchitecture.keySet().size()));
                }
                if (workflowJob.inputs.get(0).amount == 0) {
                    workflowJob.ca.workflowQueue.add(workflowJob);
                }
            }
            if (workflowJob.id.contains("actuator")) {
                if (workflowJob.actuator == null) {
                    Random r = new Random();
                    workflowJob.actuator = Actuator.allActuators.get(r.nextInt(Actuator.allActuators.size()));
                }
                if (workflowJob.inputs.get(0).amount == 0) {
                    workflowJob.actuator.actuatorWorkflowQueue.add(workflowJob);
                }

                if (Timed.getFireCount() > 0) {
                    // this.actuatorReAssign(workflowJob, Actuator.allActuator.get(0));
                }
            }
        }
    }

    @Override
    public void init() {
        for (ComputingAppliance ca : workflowArchitecture.keySet()) {
            Instance i = workflowArchitecture.get(ca);
            ca.iaas.repositories.get(0).registerObject(i.va);
            try {
                ca.workflowVMs.add(ca.iaas.requestVM(i.va, i.arc, ca.iaas.repositories.get(0), 1)[0]);
            } catch (VMManagementException e) {
                e.printStackTrace();
            }
        }

        for (ComputingAppliance ca : workflowArchitecture.keySet()) {
            ca.workflowQueue = new PriorityQueue<WorkflowJob>(new WorkflowComperator());
        }
        for (Actuator a : actuatorArchitecture) {
            a.actuatorWorkflowQueue = new PriorityQueue<WorkflowJob>(new WorkflowComperator());
        }
    }

    class WorkflowComperator implements Comparator<WorkflowJob> {
        @Override
        public int compare(WorkflowJob o1, WorkflowJob o2) {
            return 0;
        }
    }

}
