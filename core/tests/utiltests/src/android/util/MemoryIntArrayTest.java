/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.util;

import android.os.Parcel;
import junit.framework.TestCase;
import libcore.io.IoUtils;

public class MemoryIntArrayTest extends TestCase {

    public void testSize() throws Exception {
        MemoryIntArray array = null;
        try {
            array = new MemoryIntArray(3, false);
            assertEquals("size must be three", 3, array.size());
        } finally {
            IoUtils.closeQuietly(array);
        }
    }

    public void testGetSet() throws Exception {
        MemoryIntArray array = null;
        try {
            array = new MemoryIntArray(3, false);

            array.set(0, 1);
            array.set(1, 2);
            array.set(2, 3);

            assertEquals("First element should be 1", 1, array.get(0));
            assertEquals("First element should be 2", 2, array.get(1));
            assertEquals("First element should be 3", 3, array.get(2));
        } finally {
            IoUtils.closeQuietly(array);
        }
    }

    public void testWritable() throws Exception {
        MemoryIntArray array = null;
        try {
            array = new MemoryIntArray(3, true);
            assertTrue("Must be mutable", array.isWritable());
        } finally {
            IoUtils.closeQuietly(array);
        }
    }

    public void testClose() throws Exception {
        MemoryIntArray array = null;
        try {
            array = new MemoryIntArray(3, false);
            array.close();
            assertTrue("Must be closed", array.isClosed());
        } finally {
            if (array != null && !array.isClosed()) {
                IoUtils.closeQuietly(array);
            }
        }
    }

    public void testMarshalledGetSet() throws Exception {
        MemoryIntArray firstArray = null;
        MemoryIntArray secondArray = null;
        try {
            firstArray = new MemoryIntArray(3, false);

            firstArray.set(0, 1);
            firstArray.set(1, 2);
            firstArray.set(2, 3);

            Parcel parcel = Parcel.obtain();
            parcel.writeParcelable(firstArray, 0);
            parcel.setDataPosition(0);
            secondArray = parcel.readParcelable(null);
            parcel.recycle();

            assertNotNull("Should marshall file descriptor", secondArray);

            assertEquals("First element should be 1", 1, secondArray.get(0));
            assertEquals("First element should be 2", 2, secondArray.get(1));
            assertEquals("First element should be 3", 3, secondArray.get(2));
        } finally {
            IoUtils.closeQuietly(firstArray);
            IoUtils.closeQuietly(secondArray);
        }
    }

    public void testInteractOnceClosed() throws Exception {
        MemoryIntArray array = null;
        try {
            array = new MemoryIntArray(3, false);
            array.close();

            array.close();

            try {
                array.size();
                fail("Cannot interact with a closed instance");
            } catch (IllegalStateException e) {
                /* expected */
            }

            try {
                array.get(0);
                fail("Cannot interact with a closed instance");
            } catch (IllegalStateException e) {
                /* expected */
            }

            try {
                array.set(0, 1);
                fail("Cannot interact with a closed instance");
            } catch (IllegalStateException e) {
                /* expected */
            }

            try {
                array.isWritable();
                fail("Cannot interact with a closed instance");
            } catch (IllegalStateException e) {
                /* expected */
            }
        } finally {
            if (array != null && !array.isClosed()) {
                IoUtils.closeQuietly(array);
            }
        }
    }

    public void testInteractPutOfBounds() throws Exception {
        MemoryIntArray array = null;
        try {
            array = new MemoryIntArray(3, false);

            try {
                array.get(-1);
                fail("Cannot interact out of array bounds");
            } catch (IndexOutOfBoundsException e) {
                /* expected */
            }

            try {
                array.get(3);
                fail("Cannot interact out of array bounds");
            } catch (IndexOutOfBoundsException e) {
                /* expected */
            }

            try {
                array.set(-1, 0);
                fail("Cannot interact out of array bounds");
            } catch (IndexOutOfBoundsException e) {
                /* expected */
            }

            try {
                array.set(3, 0);
                fail("Cannot interact out of array bounds");
            } catch (IndexOutOfBoundsException e) {
                /* expected */
            }
        } finally {
            IoUtils.closeQuietly(array);
        }
    }

    public void testOverMaxSize() throws Exception {
        MemoryIntArray array = null;
        try {
            array = new MemoryIntArray(MemoryIntArray.getMaxSize() + 1, false);
            fail("Cannot use over max size");
        } catch (IllegalArgumentException e) {
            /* expected */
        } finally {
            IoUtils.closeQuietly(array);
        }
    }
}
