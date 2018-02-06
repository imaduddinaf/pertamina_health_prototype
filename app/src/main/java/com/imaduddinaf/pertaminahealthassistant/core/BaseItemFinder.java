package com.imaduddinaf.pertaminahealthassistant.core;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;

public interface BaseItemFinder<T> {
    ArrayList<T> findAll();
}
