/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)VehicleRunnable.java	1.4 06/02/11
 */

package com.sun.ts.tests.common.vehicle;

import java.util.*;
import com.sun.javatest.*;

public interface VehicleRunnable {
    public Status run(String[] argv, Properties p);
}
