file:///C:/Users/Leati/OneDrive/Documents/Master1%20MIAGE/Programmation%20Objet/Projet/Shopping-application/Projet%20Shop/src/main/java/com/shop/projet_shop/App.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
uri: file:///C:/Users/Leati/OneDrive/Documents/Master1%20MIAGE/Programmation%20Objet/Projet/Shopping-application/Projet%20Shop/src/main/java/com/shop/projet_shop/App.java
text:
```scala
package com.shop.projet_shop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage window;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("cartui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("La parisienne k");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getWindow() {
        return window;
    }
}
```



#### Error stacktrace:

```
scala.collection.Iterator$$anon$19.next(Iterator.scala:973)
	scala.collection.Iterator$$anon$19.next(Iterator.scala:971)
	scala.collection.mutable.MutationTracker$CheckedIterator.next(MutationTracker.scala:76)
	scala.collection.IterableOps.head(Iterable.scala:222)
	scala.collection.IterableOps.head$(Iterable.scala:222)
	scala.collection.AbstractIterable.head(Iterable.scala:935)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:164)
	dotty.tools.pc.MetalsDriver.run(MetalsDriver.scala:45)
	dotty.tools.pc.WithCompilationUnit.<init>(WithCompilationUnit.scala:31)
	dotty.tools.pc.SimpleCollector.<init>(PcCollector.scala:345)
	dotty.tools.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector$lzyINIT1(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:88)
	dotty.tools.pc.ScalaPresentationCompiler.semanticTokens$$anonfun$1(ScalaPresentationCompiler.scala:109)
```
#### Short summary: 

java.util.NoSuchElementException: next on empty iterator