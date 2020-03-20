# SootSkeleton

Generate a static analyzer based on user-provided example input-output pairs.
The analysis are implemented using Soot framework. See implementation for details on configuration inference algorithm.

## Build

Clone/Symlink the `android-platforms` folder (https://github.com/izgzhen/android-platforms):

```
ln -s path/to/android-platforms .
```

Build the project and run tests:

```
make test

make
```

For more resources, please check [wiki](https://github.com/MarkGaox/soot-skeleton/wiki).

## Usage

Examples of Using SootSkeleton

```bash
# Usage of generator mode
java -jar target/soot-skeleton-1.0-SNAPSHOT-jar-with-dependencies.jar -cfg src/test/resources/config/config.yaml -exp src/test/resources/config/examples.yaml

# Usage of runner mode
java -jar target/soot-skeleton-1.0-SNAPSHOT-jar-with-dependencies.jar -r src/test/resources/config/loadConfigIFDS.yaml src/test/resources/results/result.yaml
```

### Command-line Options

There are two separate modes in SootSkeleton: `generator mode` and `runner mode`. By providing generator mode SootSkeleton with initial 
parameters and formatted examples, it'll infer the most desirable configuration for Soot framework and generate the corresponding
output in the path that was specified by the initial configuration (for more [examples](examples)). On the other hand, in the runner mode,
SootSkeleton will request initial parameters and the Soot configuration that is produced by generator. By accepting these info, 
Skeleton will analyze the given target in the context of previously generated configuration and spawn the output in the
commandline. Following options are the commandline options SootSkeleton provides. Note that executing runner mode requests
the previous configuration result that is produced by the generator. 

``` text
Runner Mode Options
    -r arg1 arg2:
        This Option indicates that you want to use the runner mode of SootSkeleton.
        And you need to pass two parameter. The first parameter should indicate the path to the initial loading
        parameters. The second parameter should indicate the path to the Soot Configuration that was produced by
        priously run of generator.

Generator Mode Options:
    -cfg arg1:
        This is the option to indicate the path to the initial parameter.
    -exp arg1:
        This is the option to specify the path to the indicative examples.

Note: both "-cfg arg1" and "-exp arg1" should be included for generator to perform correctly.
```
More examples will be provided in the next section.

### <a name="example"></a> Example of Initial Configuration 

The initial configuration is used by both the generator and runner. It is written in YAML. Following YAML file
piece is a complete example SootSkeleton can accept.

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

### Example of input-output Example Pairs

Input-output example pairs are the generator's input that expresses the user's need. It is written in YAML. The content of example input should be 
crafted in following format. NOTE: double quote or single quote don't make any difference.

```yaml
allClasses:
  <@target class name>:
    "<@ input1>": ['<@ output1>', '<@ output2>', ...]
    "<@ input2>": ['<@ output1>', '<@ output2>', ...]

```

Following piece of yaml file is an instance that provides examples of DemoClass's call graphs.
The input of this example is the caller methods' full signature and the output is the callee methods' 
full signature. This example indicates users are trying to analyze the call graph in the context of DemoClass.
And the the output arrays are the output that they hope to see in the end.

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
