# soot-skeleton
Automatically enumerates some options in a Soot based on user determined example output and input and generates a starter script.

Clone/Symlink the `android-platforms` folder (https://github.com/izgzhen/android-platforms):

```
ln -s path/to/android-platforms .
```
Build up project:
```
make
```
For more resources, please check wiki.

## Example

TODO: explain more about these configuration's content.

### Commandline Options
```
Runner Mode Options
    -r arg1 arg2:
        This Option indicates that you want to use the runner mode of SootSkeleton.
        And you need to pass two parameter. The first parameter should indicate the path to the initial loading
        configuration. The second parameter should indicate the path to the Soot Configuration.
 
          
Generator Mode Options:
    -cfg arg1:
        This is the option to indicate the path towards the configuration(confige.yaml as describing in next part).
    -exp arg1:
        This is the option to the path towards the indicative examples.
        
     Note:
        Both "-cfg arg1" and "-exp arg1" should be included for generator to perform correctly.   
```

### Format of initial configuration 
```
This is the general form of the initial configuration (those lines that start with "#" are all comments, you can ignore it whe
when write your own files). This file's path should be passed in each run (for both Generator and runner, its path
should be indicated in the arg1). Also note that the name of the file should be <name>.yaml.

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

### Format of Examples
```
Examples should also be included in certain format. It should be indicated by the methods, and its corresponding
statement. Also note that the name of the file should be <name>.yaml.

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