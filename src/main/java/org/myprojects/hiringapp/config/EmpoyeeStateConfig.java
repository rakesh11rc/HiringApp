package org.myprojects.hiringapp.config;

import lombok.extern.slf4j.Slf4j;
import org.myprojects.hiringapp.model.EmployeeState;
import org.myprojects.hiringapp.model.EmployeeStateEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;


import java.util.Optional;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class EmpoyeeStateConfig extends EnumStateMachineConfigurerAdapter<EmployeeState, EmployeeStateEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<EmployeeState, EmployeeStateEvent> config) throws Exception {
        config.withConfiguration()
                .listener(listener())
                .autoStartup(true);
    }

    private StateMachineListener<EmployeeState, EmployeeStateEvent> listener() {

        return new StateMachineListenerAdapter<EmployeeState, EmployeeStateEvent>() {
            @Override
            public void eventNotAccepted(Message<EmployeeStateEvent> event) {
                log.error("Not accepted event: {}", event);
            }

            @Override
            public void transition(Transition<EmployeeState, EmployeeStateEvent> transition) {
                log.warn("MOVE from: {}, to: {}",
                        ofNullableState(transition.getSource()),
                        ofNullableState(transition.getTarget()));
            }

            private Object ofNullableState(State s) {
                return Optional.ofNullable(s)
                        .map(State::getId)
                        .orElse(null);
            }
        };
    }

    @Override
    public void configure(StateMachineStateConfigurer<EmployeeState, EmployeeStateEvent> states) throws Exception {
        states.withStates()
                .initial(EmployeeState.ADDED)
                .state(EmployeeState.INCHECK)
                .state(EmployeeState.APPROVED)
                .end(EmployeeState.ACTIVE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EmployeeState, EmployeeStateEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(EmployeeState.ADDED)
                .target(EmployeeState.INCHECK)
                .event(EmployeeStateEvent.CHECK)
                .and()
                .withExternal()
                .source(EmployeeState.INCHECK)
                .target(EmployeeState.APPROVED)
                .event(EmployeeStateEvent.APPROVE)
                .and()
                .withExternal()
                .source(EmployeeState.APPROVED)
                .target(EmployeeState.ACTIVE)
                .event(EmployeeStateEvent.ACTIVATE);
    }

}
