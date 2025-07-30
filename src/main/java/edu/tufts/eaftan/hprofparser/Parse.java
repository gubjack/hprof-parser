/*
 * Copyright 2014 Edward Aftandilian. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2014 Edward Aftandilian. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.tufts.eaftan.hprofparser;

import edu.tufts.eaftan.hprofparser.handler.examples.PrintHandler;

import edu.tufts.eaftan.hprofparser.handler.RecordHandler;
import edu.tufts.eaftan.hprofparser.parser.HprofParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Parse {
  
  private static final Class<? extends RecordHandler> DEFAULT_HANDLER = PrintHandler.class; 

  public static void main(String[] args) {
    
    List<String> argList = newArrayList(args);

    if (argList.size() < 1) {
      System.out.println("Usage: java Parse [--handler=<handler class>] inputfile");
      System.exit(1);
    }
    
    Class<? extends RecordHandler> handlerClass = DEFAULT_HANDLER;
    for (String arg : argList) {
      if (arg.startsWith("--handler=")) {
        String handlerClassName = arg.substring("--handler=".length());
        try {
          handlerClass = (Class<? extends RecordHandler>) Class.forName(handlerClassName);
        } catch (ClassNotFoundException e) {
          System.err.println("Could not find class " + handlerClassName);
          System.exit(1);
        }
      }
    }

    RecordHandler handler = null;
    try {
      handler = handlerClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      System.err.println("Could not instantiate " + handlerClass);
      System.exit(1);
    }
    HprofParser parser = new HprofParser(handler);

    try {
      parser.parse(new File(argList.get(argList.size() - 1)));
    } catch (IOException e) {
      System.err.println(e);
    } 

  }
  public static <E> ArrayList<E> newArrayList(E... elements) {
    checkNotNull(elements); // for GWT
    // Avoid integer overflow when a large array is passed in
    int capacity = computeArrayListCapacity(elements.length);
    ArrayList<E> list = new ArrayList<E>(capacity);
    Collections.addAll(list, elements);
    return list;
  }
  public static <T> T checkNotNull(T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }
  static int computeArrayListCapacity(int arraySize) {
    checkNonnegative(arraySize, "arraySize");

    // TODO(kevinb): Figure out the right behavior, and document it
    return saturatedCast(5L + arraySize + (arraySize / 10));
  }
  static int checkNonnegative(int value, String name) {
    if (value < 0) {
      throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
    }
    return value;
  }
  public static int saturatedCast(long value) {
    if (value > Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    if (value < Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    return (int) value;
  }

}
