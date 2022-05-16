package com.alinebatch.alinebatchwd.analytics;

import com.alinebatch.alinebatchwd.analytics.postProcess.PostProcessAnalysis;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class AnalysisContainer {

    private ArrayList<AnalysisWrite<?,?>> listOfAnalyzers = new ArrayList<>();

    private ArrayList<BasicWriter<?>> listOfBasics = new ArrayList<>();

    private ArrayList<PostProcessAnalysis> listOfPostProcessors = new ArrayList<>();

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
}
