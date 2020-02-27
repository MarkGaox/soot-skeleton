# Soot Skeleton

Generate a static analyzer based on user-provided example input-output pairs.
The analysis are implemented using Soot framework. See implementation for details on configuration inference algorithm.

## Build

Clone/Symlink the `android-platforms` folder (https://github.com/izgzhen/android-platforms):

```
ln -s path/to/android-platforms .
```

Build the project and run tests:

```
make
```

For more resources, please check [wiki](https://github.com/MarkGaox/soot-skeleton/wiki).

## Usage

TODO: provide a command line example run as well.

### Command-line Options

TODO: You didn't explain what is runner/generator mode and how to switch between them.

```
Runner Mode Options
    -r arg1 arg2:
        This Option indicates that you want to use the runner mode of SootSkeleton.
        And you need to pass two parameter. The first parameter should indicate the path to the initial loading
        configuration. The second parameter should indicate the path to the Soot Configuration.


Generator Mode Options:
    -cfg arg1:
        This is the option to indicate the path towards the configuration(config.yaml as describing in next part).
    -exp arg1:
        This is the option to the path towards the indicative examples.

     Note:
        Both "-cfg arg1" and "-exp arg1" should be included for generator to perform correctly.
```

### Example generator configuration

The configuration is used by generator during inference etc. It is written in YAML.

```yaml
# To indicate whether input is an apk or a java source file.
apk: "false"
javaClass: "true"
# To indicate the target file is in which folder.
pathToTargetDirectory: "test-resource"
# This options stands for the class name of the java target.
className: "DemoClass"
# The ouptut path for the generated configuration.
outputPath: "result2.yaml"
```

### Example input-output example pairs

This is generator's input that expresses the user's need. It is written in YAML.

TODO: What is input in this file? What is output? What does the following example do?

```yaml
# examples that users are looking forward to seeing inside the configuration
allClasses:
  DemoClass:
    "<DemoClass: void overloadTester()>":
                                          ['<DemoClass: void overload(byte)>',
                                          '<DemoClass: void overload(boolean)>',
                                          '<DemoClass: void overload(char)>',
                                          '<DemoClass: void overload(double)>',
                                          '<DemoClass: void overload(float)>',
                                           '<DemoClass: void overload(long)>',
                                          '<DemoClass: void overload(int)>',
                                          '<DemoClass: void overload(short)>']
    "<DemoClass: void main(java.lang.String[])>":
                                          ["<DemoClass: void <init>()>",
                                          "<DemoClass: void i2()>",
                                          "<DemoClass: void overloadTester()>"]

```
