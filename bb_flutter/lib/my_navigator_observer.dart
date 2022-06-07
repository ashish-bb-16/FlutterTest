import 'package:flutter/cupertino.dart';

class MyNavigatorObserver extends NavigatorObserver {
  List<Route<dynamic>> routeStack = List.empty();

  @override
  void didPush(Route<dynamic>? route, Route<dynamic>? previousRoute) {
    print("Navigator stack did Push: $route , $previousRoute");
    routeStack.add(route!);
  }

  @override
  void didPop(Route<dynamic>? route, Route<dynamic>? previousRoute) {
    print("Navigator stack did pop: $route , $previousRoute");
    routeStack.removeLast();
  }

  @override
  void didRemove(Route<dynamic>? route, Route<dynamic>? previousRoute) {
    print("Navigator stack did remove: $route , $previousRoute");
    routeStack.removeLast();
  }

  @override
  void didReplace({Route<dynamic>? newRoute, Route<dynamic>? oldRoute}) {
    print("Navigator stack did replace: $newRoute , $oldRoute");
    routeStack.removeLast();
    routeStack.add(newRoute!);
  }
}