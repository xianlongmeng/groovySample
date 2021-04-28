class MyScript1 extends Script {

    String testScript11() {
        "testScript1,test2"
    }

    @Override
    Object run() {
        return null
    }
    @Override
    Object invokeMethod(String name, Object args){
        if (!name?.trim()){
            return super.invokeMethod(name,args)
        }
        if ("testScript1" == name){
            return "testScript1"
        }else if ("test2" == name){
            return "test2"
        }
        return super.invokeMethod(name,args)
    }
}

