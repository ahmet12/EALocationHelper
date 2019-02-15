package com.ahmetkilic.ealocationhelper;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.ahmetkilic.ealocationhelper.FunctionType.LAST_LOCATION;
import static com.ahmetkilic.ealocationhelper.FunctionType.LOCATION_UPDATES;

/**
 * Created by Ahmet Kılıç on 15.02.2019.
 * Copyright © 2019. All rights reserved.
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({LOCATION_UPDATES, LAST_LOCATION})
public @interface FunctionType {
    int LOCATION_UPDATES = 1;
    int LAST_LOCATION = 2;
}
