ECLIPSE ANDROID PROJECT IMPORT SUMMARY
======================================

Manifest Merging:
-----------------
Your project uses libraries that provide manifests, and your Eclipse
project did not explicitly turn on manifest merging. In Android Gradle
projects, manifests are always merged (meaning that contents from your
libraries' manifests will be merged into the app manifest. If you had
manually copied contents from library manifests into your app manifest
you may need to remove these for the app to build correctly.

Ignored Files:
--------------
The following files were *not* copied into the new Gradle project; you
should evaluate whether these are still needed in your project and if
so manually move them:

* .DS_Store
* .gitignore
* .idea/
* .idea/.name
* .idea/Caluma.iml
* .idea/compiler.xml
* .idea/copyright/
* .idea/copyright/profiles_settings.xml
* .idea/misc.xml
* .idea/modules.xml
* .idea/vcs.xml
* .idea/workspace.xml
* LICENSE
* README.md
* caluma_launcher-web.png
* cauma_launcher-web.png
* proguard-project.txt

Replaced Jars with Dependencies:
--------------------------------
The importer recognized the following .jar files as third party
libraries and replaced them with Gradle dependencies instead. This has
the advantage that more explicit version information is known, and the
libraries can be updated automatically. However, it is possible that
the .jar file in your project was of an older version than the
dependency we picked, which could render the project not compileable.
You can disable the jar replacement in the import wizard and try again:

android-support-v4.jar => com.android.support:support-v4:19.1.0
android-support-v7-appcompat.jar => com.android.support:appcompat-v7:19.1.0
gson-2.3.jar => com.google.code.gson:gson:2.3

Potentially Missing Dependency:
-------------------------------
When we replaced the following .jar files with a Gradle dependency, we
inferred the dependency version number from the filename. This
specific version may not actually be available from the repository.
If you get a build error stating that the dependency is missing, edit
the version number to for example "+" to pick up the latest version
instead. (This may require you to update your code if the library APIs
have changed.)

gson-2.3.jar => version 2.3 in com.google.code.gson:gson:2.3

Replaced Libraries with Dependencies:
-------------------------------------
The importer recognized the following library projects as third party
libraries and replaced them with Gradle dependencies instead. This has
the advantage that more explicit version information is known, and the
libraries can be updated automatically. However, it is possible that
the source files in your project were of an older version than the
dependency we picked, which could render the project not compileable.
You can disable the library replacement in the import wizard and try
again:

android-support-v7-appcompat => [com.android.support:appcompat-v7:19.1.0]
google-play-services_lib => [com.google.android.gms:play-services:+]

Moved Files:
------------
Android Gradle projects use a different directory structure than ADT
Eclipse projects. Here's how the projects were restructured:

* AndroidManifest.xml => app/src/main/AndroidManifest.xml
* assets/ => app/src/main/assets
* Changed after for the corrected gradle dependence
  * libs/android-support-v13.jar => app/libs/android-support-v13.jar
  * libs/retrofit-1.7.0.jar => app/libs/retrofit-1.7.0.jar
* res/ => app/src/main/res/
* src/ => app/src/main/java/
* src/.DS_Store => app/src/main/resources/.DS_Store
* src/com/.DS_Store => app/src/main/resources/com/.DS_Store
* src/com/tuxskar/.DS_Store => app/src/main/resources/com/tuxskar/.DS_Store
* src/com/tuxskar/caluma/.DS_Store => app/src/main/resources/com/tuxskar/caluma/.DS_Store

Next Steps:
-----------
You can now build the project. The Gradle project needs network
connectivity to download dependencies.

Bugs:
-----
If for some reason your project does not build, and you determine that
it is due to a bug or limitation of the Eclipse to Gradle importer,
please file a bug at http://b.android.com with category
Component-Tools.

(This import summary is for your information only, and can be deleted
after import once you are satisfied with the results.)
