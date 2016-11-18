#!/bin/sh
java -Dfile.encoding=UTF-8 -classpath bin:lib/org.eclipse.recommenders.jayes/target/classes:lib/org.eclipse.recommenders.jayes.io/target/classes:lib/org.eclipse.recommenders.jayes.transformation/target/classes:lib/commons-math-2.2.jar:lib/commons-lang3-3.4.jar:lib/commons-io-2.4.jar:lib/guava-19.0-rc2.jar $1
