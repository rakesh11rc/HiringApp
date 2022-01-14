package org.myprojects.hiringapp.config;

import org.junit.jupiter.api.Test;
import org.myprojects.hiringapp.model.EmployeeState;
import org.myprojects.hiringapp.model.EmployeeStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class EmployeeStateConfigTest {

    @Autowired
    StateMachineFactory<EmployeeState, EmployeeStateEvent> stateMachineFactory;

    @Test
    public void testInitialState() throws Exception {
        StateMachine<EmployeeState, EmployeeStateEvent> stateMachine = stateMachineFactory.getStateMachine();

        StateMachineTestPlan<EmployeeState, EmployeeStateEvent> plan =
                StateMachineTestPlanBuilder.<EmployeeState, EmployeeStateEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(EmployeeState.ADDED)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void testInCheckState() throws Exception {
        StateMachine<EmployeeState, EmployeeStateEvent> stateMachine = stateMachineFactory.getStateMachine();


        StateMachineTestPlan<EmployeeState, EmployeeStateEvent> plan =
                StateMachineTestPlanBuilder.<EmployeeState, EmployeeStateEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(EmployeeState.ADDED)
                        .and()
                        .step()
                        .sendEvent(EmployeeStateEvent.CHECK)
                        .expectState(EmployeeState.INCHECK)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void testApprovedState() throws Exception {
        StateMachine<EmployeeState, EmployeeStateEvent> stateMachine = stateMachineFactory.getStateMachine();


        StateMachineTestPlan<EmployeeState, EmployeeStateEvent> plan =
                StateMachineTestPlanBuilder.<EmployeeState, EmployeeStateEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .and()
                        .step()
                        .sendEvent(EmployeeStateEvent.CHECK)
                        .and()
                        .step()
                        .sendEvent(EmployeeStateEvent.APPROVE)
                        .expectState(EmployeeState.APPROVED)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void testActiveState() throws Exception {
        StateMachine<EmployeeState, EmployeeStateEvent> stateMachine = stateMachineFactory.getStateMachine();


        StateMachineTestPlan<EmployeeState, EmployeeStateEvent> plan =
                StateMachineTestPlanBuilder.<EmployeeState, EmployeeStateEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .and()
                        .step()
                        .sendEvent(EmployeeStateEvent.CHECK)
                        .and()
                        .step()
                        .sendEvent(EmployeeStateEvent.APPROVE)
                        .and()
                        .step()
                        .sendEvent(EmployeeStateEvent.ACTIVATE)
                        .expectState(EmployeeState.ACTIVE)
                        .and()
                        .build();
        plan.test();
    }

    @Test()
    public void testSkipStateThrowsException() {
        StateMachine<EmployeeState, EmployeeStateEvent> stateMachine = stateMachineFactory.getStateMachine();

        AssertionError exception = assertThrows(AssertionError.class, () -> {
            StateMachineTestPlan<EmployeeState, EmployeeStateEvent> plan =
                    StateMachineTestPlanBuilder.<EmployeeState, EmployeeStateEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .and()
                            .step()
                            .sendEvent(EmployeeStateEvent.APPROVE)
                            .expectState(EmployeeState.APPROVED)
                            .and()
                            .build();
            plan.test();
        });


        assertThat(exception.toString()).contains("Expected: iterable with items [is <APPROVED>] in any order");
    }



}
