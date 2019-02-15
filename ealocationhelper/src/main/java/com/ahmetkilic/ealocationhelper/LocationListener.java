package com.ahmetkilic.ealocationhelper;

import android.location.Location;

/**
 * Created by Ahmet Kılıç on 15.02.2019.
 * Copyright © 2019. All rights reserved.
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */
public interface LocationListener {
    void onLocationChanged(Location location);
}
