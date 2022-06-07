import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Settings extends StatefulWidget {
  Settings({Key? key}) : super(key: key){
  }

  @override
  State<Settings> createState() => _SettingsState();
}

class _SettingsState extends State<Settings> {

  @override
  Widget build(BuildContext context) {
    print("Pushed setting screen");
    return WillPopScope(
      onWillPop: () {
        print("Popping Setting screen");
        if (Navigator.canPop(context)) {
          print("popping something");
          Navigator.pop(context);
        } else {
          SystemNavigator.pop();
        }
        return Future.value(true);
      },
      child: Scaffold(
        appBar: AppBar(title: const Text("Settings")),
        body: Center(
          child: Text("Settings"),
        ),
      ),
    );
  }

  @override
  void dispose() {
    print("Disposes Setting screen");
    super.dispose();
  }
}