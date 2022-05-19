package com.alinebatch.alinebatchwd.analytics;

import com.alinebatch.alinebatchwd.TestInjectionLambda;
import com.alinebatch.alinebatchwd.analytics.postProcess.PostProcessAnalysis;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class AnalysisContainer {

    private ArrayList<AnalysisWrite<?,?>> listOfAnalyzers = new ArrayList<>();

    private ArrayList<BasicWriter<?>> listOfBasics = new ArrayList<>();

    private ArrayList<PostProcessAnalysis> listOfPostProcessors = new ArrayList<>();

    private ArrayList<ListAnalysisWriter<?>> listOfListAnalyzers = new ArrayList<>();

    private static AnalysisContainer instance;

    private static AnalysisContainer getInstance()
    {
        if (instance == null)
        {
            synchronized (AnalysisContainer.class)
            {
                if (instance == null)
                {
                    instance = new AnalysisContainer();
                }
            }
        }
        return instance;
    }

    public static void add(AnalysisWrite<?,?> analysisWrite)
    {
        log.info("Adding to analysis write queue: " + analysisWrite.getClass());

        getInstance().listOfAnalyzers.add(analysisWrite);
    }

    public static void addList(ListAnalysisWriter<?> lister)
    {
        log.info("Adding to analysis write queue: " + lister.getClass());

        getInstance().listOfListAnalyzers.add(lister);
    }

    public static void addBasic(BasicWriter<?> basicWriter)
    {
        getInstance().listOfBasics.add(basicWriter);
    }

    public static void addPost(PostProcessAnalysis item)
    {
        getInstance().listOfPostProcessors.add(item);
    }

    public static void postProcess()
    {
        getInstance().listOfPostProcessors.forEach((a) ->
        {
            a.postProcess();
        });
    }

    public static void write()
    {
        getInstance().listOfAnalyzers.forEach((a) ->
        {
            try
            {
                a.write();
            } catch (Exception e)
            {
                log.info(e.getMessage());
            }

        });

        getInstance().listOfListAnalyzers.forEach((a) ->
        {
            try
            {
                a.write();
            } catch (Exception e)
            {
                log.info(e.getMessage());
            }

        });

        getInstance().listOfBasics.forEach((a) ->
        {
            try
            {
                a.write();
            } catch (Exception e)
            {
                log.info(e.getMessage());
            }

        });
    }

    public static AnalysisWrite<?,?> grabProcessor(Class toGrab)
    {
        AtomicReference<AnalysisWrite<?, ?>> returner = new AtomicReference<>();

        getInstance().listOfAnalyzers.forEach((k) ->{
            if (k.getClass().equals(toGrab))
            {
                returner.set(k);
            }
        });
        return returner.get();
    }

    public static BasicWriter<?> grabBasic(Class toGrab)
    {
        AtomicReference<BasicWriter<?>> returner = new AtomicReference<>();
        getInstance().listOfBasics.forEach((k) ->
        {
            if (k.getClass().equals(toGrab))
            {
                returner.set(k);
            }
        });
        return returner.get();
    }

    public static boolean injectTest(TestInjectionLambda method, Class toTest)
    {
        AtomicBoolean allGood = new AtomicBoolean(true);
        getInstance().listOfAnalyzers.forEach((k) ->
        {
            if (k.getClass().equals(toTest))
            {
                k.analysisMap.entrySet().forEach((e) ->
                {
                    if (!method.injectedLogic(e)) allGood.set(false);
                });
            }
        });
        return allGood.get();
    }
}
