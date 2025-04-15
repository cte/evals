def append(list1, list2):
    # Avoid modifying list1 directly, create a new list
    result = []
    for item in list1:
        result.append(item) # Using list.append is generally allowed unless explicitly forbidden
    for item in list2:
        result.append(item)
    return result


def concat(lists):
    result = []
    for inner_list in lists:
        for item in inner_list:
            result.append(item)
    return result


def filter(function, lst): # Renamed list to lst to avoid shadowing built-in
    result = []
    for item in lst:
        if function(item):
            result.append(item)
    return result


def length(lst): # Renamed list to lst
    count = 0
    for _ in lst:
        count += 1
    return count


def map(function, lst): # Renamed list to lst
    result = []
    for item in lst:
        result.append(function(item))
    return result


def foldl(function, lst, initial): # Renamed list to lst
    accumulator = initial
    for item in lst:
        accumulator = function(accumulator, item)
    return accumulator


def foldr(function, lst, initial): # Renamed list to lst
    accumulator = initial
    # Iterate backwards through the list
    for i in range(length(lst) - 1, -1, -1):
         accumulator = function(accumulator, lst[i])
    # Alternative using reverse first, but let's stick to iteration
    # reversed_list = reverse(lst)
    # for item in reversed_list:
    #     accumulator = function(item, accumulator)
    return accumulator


def reverse(lst): # Renamed list to lst
    result = []
    # Iterate backwards
    for i in range(length(lst) - 1, -1, -1):
        result.append(lst[i])
    return result
