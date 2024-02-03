
# Android virtual joystick

A library made in kotlin for native android that exposes a view for a virtual joystick. Customizable and simple to use.

## Installation

To use the library you can install it from [github packages](#github-packages-installation) or [locally](#local-installation).

### Github Packages installation

#### Step 1: Generate a Personal Access Token for GitHub

* Inside you GitHub account:
  * Settings -> Developer Settings -> Personal Access Tokens -> Generate new token
  * Make sure you select the following scopes ("read:packages") and Generate a token
  * After Generating make sure to copy your new personal access token. You won’t be able to see it again!

#### Step 2: Store your GitHub - Personal Access Token details

* Create a github.properties file within your root Android project
* In case of a public repository make sure you add this file to .gitignore for keep the token private
  * Add properties gpr.usr=GITHUB_USERID and gpr.key=PERSONAL_ACCESS_TOKEN
  * Replace GITHUB_USERID with personal / organisation Github User ID and PERSONAL_ACCESS_TOKEN with the token generated in #Step 1
> Alternatively you can also add the **GPR_USER** and **GPR_API_KEY** values to your environment variables on you local machine or build server to avoid creating a github properties file

#### Step 3: Update settings.gradle inside the project
* KTS

  Load you credentials before apply it after the pluginManagement config.
  ```kts
  pluginManagement {
      // .....
  }
  // ....
  val githubPropertiesFile: File = rootProject.projectDir.resolve("github.properties");
  val githubProperties = Properties()
  githubProperties.load(FileInputStream(githubPropertiesFile))
  // .....
  ```
  Add a new repository inside the dependencyResolutionManagement config.
  
  ```kts
  // ......
  dependencyResolutionManagement {
      // ......
      repositories {
          // ......
          maven {
              name = "GitHubPackages"
              url = uri("https://maven.pkg.github.com/yoimerdr/AndroidVirtualJoystick")
              credentials {
                    username = githubProperties.getProperty("gpr.usr") ?: System.getenv("GPR_USER")
                    password = githubProperties.getProperty("gpr.key") ?: System.getenv("GPR_API_KEY")
              }
          }
      }
  }
  ```
  You can read more about how to install a github packages library with KTS [here](https://github.com/enefce/AndroidLibrary-GPR-KDSL?tab=readme-ov-file#using-a-library-from-the-github-packages)
  * Groovy
    ```gradle
  
    Load you credentials before apply it after the pluginManagement config.
    // ....
    pluginManagement {
        // .....
    }
    // ....
    def githubPropertiesFile = Paths.get(rootProject.projectDir.toString(), "github.properties").toFile()
    def githubProperties = new Properties()
    githubProperties.load(new FileInputStream(githubPropertiesFile))
    // .....
    ```
    Add a new repository inside the dependencyResolutionManagement config.

    ```gradle
    // ......
    dependencyResolutionManagement {
        // ......
        repositories {
            // ......
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/yoimerdr/AndroidVirtualJoystick")
  
                credentials {
                    username = githubProperties['gpr.usr'] ?: System.getenv("GPR_USER")
                    password = githubProperties['gpr.key'] ?: System.getenv("GPR_API_KEY")
                }
            }
        }
    }
    ```
    You can read more about how to install a github packages library with groovy [here](https://github.com/enefce/AndroidLibraryForGitHubPackagesDemo?tab=readme-ov-file#using-a-library-from-the-github-package-registry)

#### Step 4: Update build.gradle inside the application module

* KTS
  ```kts
  // ....
  dependencies {
      // ....
      implementation("com.yoimerdr.android:virtualjoystick:1.0.0")
  }
  ```
  
* Groovy
  ```gradle
  // ....
  dependencies {
      // ....
      implementation("com.yoimerdr.android:virtualjoystick:1.0.0")
  }
  ```

### Local installation

#### Step 1: Download one of the release packages of the library
* You can see all the releases [here](https://github.com/yoimerdr/AndroidVirtualJoystick/releases/)
* After downloading the package, unzip it to the libs folder of the application module. The path is usually: path_to_project/app/libs.

#### Step 2: Update build.gradle inside the application module
* KTS
  ```kts
  // ....
  dependencies {
      // ....
      implementation(files("libs/yoimerdr/android/virtualjoystick/1.0.0/virtualjoystick-1.0.0.aar"))
  }
  ```
* Groovy
  ```gradle
  // ....
  dependencies {
      // ....
      implementation files('libs/yoimerdr/android/virtualjoystick/1.0.0/virtualjoystick-1.0.0.aar')
  }
  ```
  You can read a litter more about how to install an aar library [here](https://developer.android.com/studio/projects/android-library#psd-add-aar-jar-dependency)

## Usage

After installing the package, you can use it by adding the JoystickView view to the layout of your activity or view.

```xml
<!--.........-->
<com.yoimerdr.android.virtualjoystick.views.JoystickView
        android:id="@+id/vJoystick"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginBottom="16dp"
        app:arcControlDrawer_sweepAngle="100"
        app:controlDrawer_accentColor="#FFFFFF"
        app:controlDrawer_primaryColor="#FF0000"
        app:controlType="circle"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
<!--.........-->
```

And use it.

* Kotlin
```kotlin
  // ....
  val joystick = findViewById<JoystickView>(R.id.vJoystick)
  joystick.setMoveListener {
  
  }
  // ....
  ```

You can read the latest docs of the library [here](https://yoimerdr.github.io/AndroidVirtualJoystick/docs/1.0.0/index.html)