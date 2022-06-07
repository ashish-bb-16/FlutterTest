import 'package:bb_flutter/my_navigator_observer.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';

import 'cell.dart';
import 'routing.dart';

//void main() => runApp(const MyApp());

void main() {
  // This call ensures the Flutter binding has been set up before creating the
  // MethodChannel-based model.
  WidgetsFlutterBinding.ensureInitialized();

  final model = CounterModel();

  runApp(
    ChangeNotifierProvider.value(
      value: model,
      child: const MyApp(),
    ),
  );
}

/// This is on alternate entrypoint for this module to display Flutter UI in
/// a (multi-)view integration scenario.
// This is unfortunately in this file due to
// https://github.com/flutter/flutter/issues/72630.
@pragma("vm:entry-point")
void showCell() {
  runApp(const Cell());
}

class CounterModel extends ChangeNotifier {
  CounterModel() {
    _channel.setMethodCallHandler(_handleMessage);
    _channel.invokeMethod<void>('requestCounter');
  }

  final _channel = const MethodChannel('dev.flutter.example/counter');

  int _count = 0;

  int get count => _count;

  void increment() {
    _channel.invokeMethod<void>('incrementCounter');
  }

  Future<dynamic> _handleMessage(MethodCall call) async {
    if (call.method == 'reportCounter') {
      _count = call.arguments as int;
      notifyListeners();
    }
  }
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    print("build of Routing in main app");
    print("Pushed main screen");
    return WillPopScope(
        onWillPop: () {
          if (Navigator.canPop(context)) {
            print("popping something");
            Navigator.pop(context);
          } else {
            SystemNavigator.pop();
          }
          print("Popping main screen");
          return Future.value(true);
        },
        child: MaterialApp(
            //navigatorObservers: [MyNavigatorObserver()],
            title: 'Flutter Demo',
            theme: ThemeData(
              // This is the theme of your application.
              //
              // Try running your application with "flutter run". You'll see the
              // application has a blue toolbar. Then, without quitting the app, try
              // changing the primarySwatch below to Colors.green and then invoke
              // "hot reload" (press "r" in the console where you ran "flutter run",
              // or press Run > Flutter Hot Reload in a Flutter IDE). Notice that the
              // counter didn't reset back to zero; the application is not restarted.
              primarySwatch: Colors.blue,
            ),
            home: Routing()));
    return MaterialApp(
        //navigatorObservers: [MyNavigatorObserver()],
        title: 'Flutter Demo',
        theme: ThemeData(
          // This is the theme of your application.
          //
          // Try running your application with "flutter run". You'll see the
          // application has a blue toolbar. Then, without quitting the app, try
          // changing the primarySwatch below to Colors.green and then invoke
          // "hot reload" (press "r" in the console where you ran "flutter run",
          // or press Run > Flutter Hot Reload in a Flutter IDE). Notice that the
          // counter didn't reset back to zero; the application is not restarted.
          primarySwatch: Colors.blue,
        ),
        home: Routing());
  }
}
