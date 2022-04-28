package com.alinebatch.alinebatchwd.processors;

import com.alinebatch.alinebatchwd.models.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class StateCacheProcessor implements ItemProcessor<State, State> {
    @Override
    public State process(State state) throws Exception {
        return state;
    }
}
