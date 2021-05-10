package aa

class MyScript1  extends Script{

    MyScript1() {
    }

    MyScript1(Binding binding) {
        super(binding)
    }

    String testScript11() {
        "testScript1,test2"
    }


    @Override
    Object invokeMethod(String name, Object args){
        if (!name?.trim()){
            return super.invokeMethod(name,args)
        }
        if ("testScript1" == name){
            return "testScript1"
        }else if ("test2" == name){
            return aa
        }
        return super.invokeMethod(name,args)
    }

    @Override
    Object run() {
        return null
    }
}

