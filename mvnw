#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Start Up Batch script
# ----------------------------------------------------------------------------

# Resolve PRG to allow relative symlinks
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`"/$link"
  fi
done

PRGDIR=`dirname "$PRG"`

# Check Java Runtime
if [ -z "$JAVA_HOME" ] ; then
  JAVACMD=`which java 2>/dev/null || true`
  if [ -z "$JAVACMD" ] ; then
    echo "Error: JAVA_HOME is not defined and cannot find java in PATH." >&2
    echo "Please install Java 17+ or set JAVA_HOME before running." >&2
    exit 1
  fi
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

# Locate Maven Wrapper JAR or Download Maven if needed
WRAPPER_JAR="$PRGDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROP="$PRGDIR/.mvn/wrapper/maven-wrapper.properties"

# If mvn is installed on system, use it directly
if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

# Otherwise, download maven standalone if wrapper jar is missing
MAVEN_VERSION="3.9.6"
MAVEN_DIR="$HOME/.m2/wrapper/dists/apache-maven-$MAVEN_VERSION"
MAVEN_EXEC="$MAVEN_DIR/bin/mvn"

if [ ! -x "$MAVEN_EXEC" ]; then
  echo "Downloading Maven $MAVEN_VERSION to $MAVEN_DIR..."
  mkdir -p "$MAVEN_DIR"
  TMP_ZIP="/tmp/apache-maven-$MAVEN_VERSION.zip"
  curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.zip" -o "$TMP_ZIP"
  unzip -q -o "$TMP_ZIP" -d /tmp/mvn-extract
  mv /tmp/mvn-extract/apache-maven-$MAVEN_VERSION/* "$MAVEN_DIR/"
  rm -rf /tmp/mvn-extract "$TMP_ZIP"
  chmod +x "$MAVEN_EXEC"
fi

exec "$MAVEN_EXEC" "$@"

