import 'package:flutter/material.dart';

class Settings extends StatefulWidget {
  Settings({Key? key}) : super(key: key){
  }

  @override
  State<Settings> createState() => _SettingsState();
}

class _SettingsState extends State<Settings> {

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: (){
        Navigator.of(context).pop();
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
}