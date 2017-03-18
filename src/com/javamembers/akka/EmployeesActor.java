package com.javamembers.akka;


import java.util.ArrayList;
import java.util.List;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.japi.Function;


public class EmployeesActor extends UntypedActor{
	

	 int counter=0;
	 List<EmployeeData> empList= null;
	 
	@Override
	public void onReceive(Object msg) throws Exception {
		
		// create Actor reference for the sub actors
		ActorRef perRef= getContext().actorOf( Props.create(PermenantEmployeeActor.class));
		ActorRef conRef= getContext().actorOf( Props.create(ContractEmployeeActor.class));	
		
		if(msg instanceof String){
			empList=new ArrayList<EmployeeData>();
			counter=0;  				
			perRef.tell(msg, getSelf());
			conRef.tell(msg, getSelf());
		}
		
		else if(msg instanceof EmployeeDetails){
			++counter;
			System.out.println("Counter ==>"+counter);			
			EmployeeDetails details =(EmployeeDetails) msg;
			
			empList.addAll(details.getEmpDetails());           
            if(counter==1){
	            System.out.println("the products size==>"+empList.size());  
	            for(EmployeeData data : empList){
	            	System.out.println(data.getName()+","+ data.getAge()+","+data.getSex()+","+data.getEmployeeType());
	            }
            }
		}else{
            unhandled( msg );
        }
		
		
	}
	
	private static SupervisorStrategy strategy = new OneForOneStrategy(10,
            Duration.create(5000,"millis"),
            new Function<Throwable, SupervisorStrategy.Directive>() {
              
                public SupervisorStrategy.Directive apply(Throwable t) {
                    if (t instanceof ArithmeticException) {
                        return SupervisorStrategy.restart();
                    } else if (t instanceof NullPointerException) {
                    	System.out.println("In the Supervisor Stragey,resuming the actor system");
                        return SupervisorStrategy.resume();
                    } else if (t instanceof IllegalArgumentException) {
                        return SupervisorStrategy.stop();
                    } else {
                        return SupervisorStrategy.escalate();
                    }
                }
            }
    );

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
	
	
	
	

}
