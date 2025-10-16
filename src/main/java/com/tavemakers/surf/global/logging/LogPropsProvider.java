package com.tavemakers.surf.global.logging;

import java.util.Map;

public interface LogPropsProvider {
    Map<String, Object> buildProps();
}