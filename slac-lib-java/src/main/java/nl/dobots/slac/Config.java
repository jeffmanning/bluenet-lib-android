package nl.dobots.slac;

/**
 * Copyright (c) 2015 Bart van Vliet <bart@dobots.nl>. All rights reserved.
 * <p/>
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3, as
 * published by the Free Software Foundation.
 * <p/>
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * <p/>
 * Created on 17-11-15
 *
 * @author Bart van Vliet
 */
public class Config {
	public static float STANDARD_DEVIATION_HEADING = 0.1f;
	public static float STANDARD_DEVIATION_STEP = 0.15f;

	public static int INIT_NUM_PARTICLES = 200;
	public static float INIT_STANDARD_DEVIATION_RANGE = 1.0f;
	public static int INIT_NUM_RANDOM_PARTICLES = 0;
	public static float INIT_EFFECTIVE_PARTICLE_THRESHOLD = 75;
	public static float INIT_MAX_VARIANCE = 4;
}