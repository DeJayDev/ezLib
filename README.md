EZLib
==========

Helper library for Bukkit plugins development.
----

### ⚠️ This project is still actively being forked, modified, and the directions below are still out of date. 

###### (Formerly known as QuartzLib (Formerly known as zLib))

### How to use this library in your plugin?

If you are using Maven to build your plugin, follow these simple steps. Other builds methods are not supported (but you can use them of course).  
Currently, EZLib requires **Java 17** or later and **Paper 1.19**.

#### I'm starting a new plugin

Create a plugin how you normally would and add the EZLib.

#### I want to add EZLib to an existing project

1. Add our Maven repository to your `pom.xml` file.
  
    ```xml
        <repository>
            <id>EZLib</id>
            <url>https://maven.pkg.github.com/DeJayDev</url>
        </repository>
    ```

2. Add EZLib as a dependency.
  
    ```xml
        <dependency>
            <groupId>dev.dejay</groupId>
            <artifactId>ezlib</artifactId>
            <version>0.1.0</version>
        </dependency>
    ```
    
3. Add the shading plugin to the build. Replace **`YOUR.OWN.PACKAGE`** with your own package.
  
   ```xml
        <build>
            ...
            <plugins>
                ...
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-shade-plugin</artifactId>
                    <version>2.4</version>
                    <configuration>
                        <artifactSet>
                            <includes>
                                <include>fr.zcraft:quartzlib</include>
                            </includes>
                        </artifactSet>
                        <relocations>
                            <relocation>
                                <pattern>fr.zcraft.ezlib</pattern>
                                <shadedPattern>YOUR.OWN.PACKAGE</shadedPattern>
                            </relocation>
                        </relocations>
                    </configuration>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>shade</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                ...
            </plugins>
            ...
        </build>
   ```
   
4. Build your project as usual, as example with the following command from your working directory, or using an integrated tool from your IDE.
  
   ```bash
   mvn clean install
   ```

You should also update your code, so your main class extends [`EZLib`](https://zdevelopers.github.io/QuartzLib/?fr/zcraft/quartzlib/core/QuartzPlugin.html) instead of `JavaPlugin`. 
No other changes are required. This will allow you to use directly some useful methods to load your plugin's components.  
Check out [the wiki](https://github.com/zDevelopers/QuartzLib/wiki/Installation) for more information.
